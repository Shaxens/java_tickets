```toml
name = 'create'
description = 'admin route'
method = 'POST'
url = 'http://localhost:8080/api/category'
sortWeight = 3000000
id = '2d90476d-8898-42eb-ac82-d3a1794dabd2'

[auth.bearer]
token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.42zZ37xvkF0ASJsynHgmB17OM6GHHaP9Z_GQg3bMiV55UQAfUhtmos5Dkj0CGRbAFTlzQvvHM6c6IrhTzgf_rg'

[body]
type = 'JSON'
raw = '''
{
  "name": "Téléphonie"
}'''
```
