```toml
name = 'login'
description = 'public route'
method = 'POST'
url = 'http://localhost:8080/api/auth/login'
sortWeight = 1000000
id = '5507c66c-86a0-4d1e-97ba-bf9fe56eeff6'

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
name = 'Admin'
id = '6f5238df-5a52-40d6-a824-b47ed91b559b'

[body]
type = 'JSON'
raw = '''
{
  "pseudo": "admin",
  "password": "admin123"
}'''
```

### Example

```toml
name = 'User'
id = '8a7598cd-0c9f-46ef-8f94-d5a8d0329b95'

[body]
type = 'JSON'
raw = '''
{
  "pseudo": "user",
  "password": "user123"
}'''
```
