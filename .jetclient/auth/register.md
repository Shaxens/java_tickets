```toml
name = 'register'
description = 'public route'
method = 'POST'
url = 'http://localhost:8080/api/auth/register'
sortWeight = 2000000
id = 'ef2dbfe3-f5da-49a1-aef6-e88543429a95'

[body]
type = 'JSON'
raw = '''
{
  "pseudo": "",
  "password": ""
}'''
```

### Example

```toml
name = 'Add User'
id = '483261ad-80a4-423c-9504-0ee0a1a2c9f9'

[body]
type = 'JSON'
raw = '''
{
  "pseudo": "newUser",
  "password": "newUser123"
}'''
```
