```toml
name = 'create'
description = 'admin route'
method = 'POST'
url = 'http://localhost:8080/api/priority'
sortWeight = 3000000
id = '98a44c6c-e34d-4faf-8c05-d92c5ccb5f7b'

[auth.bearer]
token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.42zZ37xvkF0ASJsynHgmB17OM6GHHaP9Z_GQg3bMiV55UQAfUhtmos5Dkj0CGRbAFTlzQvvHM6c6IrhTzgf_rg'

[body]
type = 'JSON'
raw = '''
{
  "name": "Urgence"
}'''
```
