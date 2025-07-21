```toml
name = 'update'
description = 'admin route'
method = 'PUT'
url = 'http://localhost:8080/api/priority/1'
sortWeight = 4000000
id = '6397e18f-ff43-4f7f-b7e4-a5c94c240b67'

[auth.bearer]
token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.42zZ37xvkF0ASJsynHgmB17OM6GHHaP9Z_GQg3bMiV55UQAfUhtmos5Dkj0CGRbAFTlzQvvHM6c6IrhTzgf_rg'

[body]
type = 'JSON'
raw = '''
{
  "name": "Critique - P1"
}'''
```
