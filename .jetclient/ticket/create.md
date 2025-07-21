```toml
name = 'create'
description = 'private route'
method = 'POST'
url = 'http://localhost:8080/api/ticket'
sortWeight = 3000000
id = '9c47276d-e56a-4b64-b12d-0a7178dfffa8'

[auth.bearer]
token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIn0.ll1IkQYaSF4QtIOIRmVbf2msqlX6LDPLUF5xI75HylPIhWJSXdGH5RKFwlusFv-cn7sD_oF7gBvl09AsX5Ok6A'

[body]
type = 'JSON'
raw = '''
{
  "title": "Problème imprimante",
  "description": "L'imprimante du 2ème étage ne fonctionne plus",
  "priority": {
    "id": 2
  },
  "categories": [
    {
      "id": 1
    }
  ]
}'''
```

### Example

```toml
name = 'Add ticket'
id = '2641a376-76eb-4460-b0d4-42f9d77a64a9'

[body]
type = 'JSON'
raw = '''
{
  "titre": "Problème imprimante",
  "description": "L'imprimante du 2ème étage ne fonctionne plus",
  "priorite": {
    "id": 2
  },
  "categories": [
    {
      "id": 1
    }
  ]
}'''
```

### Example

```toml
name = 'No category'
id = '842b379a-5b9b-4061-be13-8a50391c1a0c'

[body]
type = 'JSON'
raw = '''
{
  "titre": "Demande d'information",
  "description": "Question sur les procédures",
  "priorite": {
    "id": 4
  }
}'''
```
