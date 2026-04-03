import json
import urllib.request
import os

def extract_schema(s, components, visited=None):
    if visited is None:
        visited = set()
        
    if not isinstance(s, dict):
        return str(s)
    
    if '$ref' in s:
        ref_name = s['$ref'].split('/')[-1]
        if ref_name in visited:
            return f"Circular reference: {ref_name}"
        visited.add(ref_name)
        schema_ref = components.get('schemas', {}).get(ref_name, {})
        result = extract_schema(schema_ref, components, visited)
        visited.remove(ref_name)
        return result
    
    if s.get('type') == 'object' or 'properties' in s:
        obj = {}
        for prop, prop_details in s.get('properties', {}).items():
            obj[prop] = extract_schema(prop_details, components, set(visited))
        return obj
        
    if s.get('type') == 'array':
        items = s.get('items', {})
        return [extract_schema(items, components, set(visited))]
        
    # primitive types
    t = s.get('type')
    if t == 'string':
        if s.get('format') == 'date-time':
            return "2023-10-10T10:10:10.000Z"
        return "string"
    elif t in ('integer', 'number'):
        return 0
    elif t == 'boolean':
        return True
    
    return "any"

def extract_schema_details(s, components, required_fields=None, visited=None):
    """Extract detailed schema properties with type and description"""
    if visited is None:
        visited = set()
    if required_fields is None:
        required_fields = []
        
    if not isinstance(s, dict):
        return []
    
    if '$ref' in s:
        ref_name = s['$ref'].split('/')[-1]
        if ref_name in visited:
            return []
        visited.add(ref_name)
        schema_ref = components.get('schemas', {}).get(ref_name, {})
        result = extract_schema_details(schema_ref, components, required_fields, visited)
        visited.remove(ref_name)
        return result
    
    if s.get('type') == 'object' or 'properties' in s:
        properties = s.get('properties', {})
        required = s.get('required', [])
        details = []
        for prop, prop_details in properties.items():
            field_type = prop_details.get('type', 'unknown')
            field_desc = prop_details.get('description', '')
            is_required = prop in required
            
            # Handle nested objects and arrays
            if field_type == 'array':
                items = prop_details.get('items', {})
                item_type = items.get('type', 'object')
                field_type = f'array[{item_type}]'
            elif field_type == 'object' or '$ref' in prop_details:
                field_type = 'object'
            
            req_status = "required" if is_required else "optional"
            details.append({
                'name': prop,
                'type': field_type,
                'required': is_required,
                'description': field_desc
            })
        return details
    
    return []

def main():
    url = "http://localhost:8080/v3/api-docs"
    print(f"Fetching {url}")
    try:
        req = urllib.request.Request(url)
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode('utf-8'))
    except Exception as e:
        print("Failed to fetch or parse JSON:", e)
        return

    markdown = '# API Documentation\n\n'
    
    paths = data.get('paths', {})
    components = data.get('components', {})

    for path, methods in paths.items():
        for method, details in methods.items():
            if method.lower() not in ['get', 'post', 'put', 'delete', 'patch']:
                continue
                
            markdown += f'## {method.upper()} {path}\n\n'
            
            if 'summary' in details:
                markdown += f'**Summary:** {details["summary"]}\n\n'
            
            if 'description' in details:
                markdown += f'{details["description"]}\n\n'
            
            # 1. Endpoint
            markdown += '### 1. Endpoint\n'
            markdown += f'`{method.upper()} {path}`\n\n'
            
            # 2. JSON Body
            markdown += '### 2. Request / Input\n'
            has_body = False
            if 'requestBody' in details:
                content = details['requestBody'].get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    
                    # Get schema details
                    schema_details = extract_schema_details(schema, components)
                    if schema_details:
                        markdown += '**Fields:**\n'
                        markdown += '| Field | Type | Required | Description |\n'
                        markdown += '|-------|------|----------|-------------|\n'
                        for field in schema_details:
                            req = '✓ Yes' if field['required'] else '✗ No'
                            desc = field['description'] or '-'
                            markdown += f"| `{field['name']}` | {field['type']} | {req} | {desc} |\n"
                        markdown += '\n'
                    
                    # Example JSON
                    dummy = extract_schema(schema, components)
                    markdown += '**Example:**\n```json\n'
                    markdown += json.dumps(dummy, indent=2) + '\n'
                    markdown += '```\n\n'
                    has_body = True
                elif 'multipart/form-data' in content:
                    schema = content['multipart/form-data'].get('schema', {})
                    
                    schema_details = extract_schema_details(schema, components)
                    if schema_details:
                        markdown += '*(multipart/form-data)*\n\n**Fields:**\n'
                        markdown += '| Field | Type | Required | Description |\n'
                        markdown += '|-------|------|----------|-------------|\n'
                        for field in schema_details:
                            req = '✓ Yes' if field['required'] else '✗ No'
                            desc = field['description'] or '-'
                            markdown += f"| `{field['name']}` | {field['type']} | {req} | {desc} |\n"
                        markdown += '\n'
                    
                    dummy = extract_schema(schema, components)
                    markdown += '**Example:**\n```json\n'
                    markdown += json.dumps(dummy, indent=2) + '\n'
                    markdown += '```\n\n'
                    has_body = True

            if not has_body:
                # Check parameters for query/path params
                params = details.get('parameters', [])
                if params:
                    markdown += '| Parameter | Type | Required | Description |\n'
                    markdown += '|-----------|------|----------|-------------|\n'
                    for p in params:
                        p_name = p.get('name')
                        p_in = p.get('in')
                        p_req = p.get('required', False)
                        p_type = p.get('schema', {}).get('type', 'string')
                        p_desc = p.get('description', '') or '-'
                        req = '✓ Yes' if p_req else '✗ No'
                        markdown += f'| `{p_name}` ({p_in}) | {p_type} | {req} | {p_desc} |\n'
                    markdown += '\n'
                else:
                    markdown += 'None\n\n'

            # 3. Response
            markdown += '### 3. Response / Output\n'
            responses = details.get('responses', {})
            for status_code, response_details in responses.items():
                markdown += f'**Status {status_code}**: {response_details.get("description", "")}\n\n'
                content = response_details.get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    
                    # Get schema details
                    schema_details = extract_schema_details(schema, components)
                    if schema_details:
                        markdown += '**Fields:**\n'
                        markdown += '| Field | Type | Description |\n'
                        markdown += '|-------|------|-------------|\n'
                        for field in schema_details:
                            desc = field['description'] or '-'
                            markdown += f"| `{field['name']}` | {field['type']} | {desc} |\n"
                        markdown += '\n'
                    
                    dummy_response = extract_schema(schema, components)
                    markdown += '**Example:**\n```json\n'
                    markdown += json.dumps(dummy_response, indent=2) + '\n'
                    markdown += '```\n\n'
                elif '*/*' in content:
                    schema = content['*/*'].get('schema', {})
                    
                    schema_details = extract_schema_details(schema, components)
                    if schema_details:
                        markdown += '**Fields:**\n'
                        markdown += '| Field | Type | Description |\n'
                        markdown += '|-------|------|-------------|\n'
                        for field in schema_details:
                            desc = field['description'] or '-'
                            markdown += f"| `{field['name']}` | {field['type']} | {desc} |\n"
                        markdown += '\n'
                    
                    dummy_response = extract_schema(schema, components)
                    markdown += '**Example:**\n```json\n'
                    markdown += json.dumps(dummy_response, indent=2) + '\n'
                    markdown += '```\n\n'
                else:
                    markdown += '`No JSON response body`\n\n'
                    
            markdown += '---\n\n'

    with open('API_DOCUMENTATION.md', 'w', encoding='utf-8') as f:
        f.write(markdown)
        
    print("API_DOCUMENTATION.md generated successfully.")

if __name__ == '__main__':
    main()
