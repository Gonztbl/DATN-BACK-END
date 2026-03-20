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
            
            # 1. Endpoint
            markdown += '### 1. Endpoint\n'
            markdown += f'`{method.upper()} {path}`\n\n'
            
            # 2. JSON Body
            markdown += '### 2. Request Body / Parameters\n'
            has_body = False
            if 'requestBody' in details:
                content = details['requestBody'].get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    dummy = extract_schema(schema, components)
                    markdown += '```json\n'
                    markdown += json.dumps(dummy, indent=2) + '\n'
                    markdown += '```\n\n'
                    has_body = True
                elif 'multipart/form-data' in content:
                    schema = content['multipart/form-data'].get('schema', {})
                    dummy = extract_schema(schema, components)
                    markdown += '*(multipart/form-data)*\n```json\n'
                    markdown += json.dumps(dummy, indent=2) + '\n'
                    markdown += '```\n\n'
                    has_body = True

            if not has_body:
                # Check parameters for query/path params
                params = details.get('parameters', [])
                if params:
                    for p in params:
                        p_name = p.get('name')
                        p_in = p.get('in')
                        p_req = p.get('required', False)
                        p_type = p.get('schema', {}).get('type', 'string')
                        markdown += f'- `{p_name}` ({p_in}, {p_type}) {"*(required)*" if p_req else ""}\n'
                    markdown += '\n'
                else:
                    markdown += '`None`\n\n'

            # 3. Response
            markdown += '### 3. Response\n'
            responses = details.get('responses', {})
            for status_code, response_details in responses.items():
                markdown += f'**Status {status_code}**: {response_details.get("description", "")}\n\n'
                content = response_details.get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    dummy_response = extract_schema(schema, components)
                    markdown += '```json\n'
                    markdown += json.dumps(dummy_response, indent=2) + '\n'
                    markdown += '```\n\n'
                elif '*/*' in content:
                    schema = content['*/*'].get('schema', {})
                    dummy_response = extract_schema(schema, components)
                    markdown += '```json\n'
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
