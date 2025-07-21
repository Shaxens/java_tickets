```toml
name = 'update'
description = 'private route'
method = 'PUT'
url = 'http://localhost:8080/api/ticket/1'
sortWeight = 4000000
id = 'a385a387-78f7-430d-8518-9bed663b67ed'

[auth.bearer]
token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIn0.ll1IkQYaSF4QtIOIRmVbf2msqlX6LDPLUF5xI75HylPIhWJSXdGH5RKFwlusFv-cn7sD_oF7gBvl09AsX5Ok6A'

[body]
type = 'JSON'
raw = '''
{
  "title": "Serveur principal - URGENT",
  "description": "Le serveur principal ne répond plus depuis ce matin. Intervention en cours.",
  "priority": {
    "id": 1
  },
  "categories": [
    {
      "id": 1
    },
    {
      "id": 4
    },
    {
      "id": 5
    }
  ]
}'''
```
