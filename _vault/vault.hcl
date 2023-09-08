storage "file" {
  path = "/vault/data"
}

listener "tcp" {
  address     = "0.0.0.0:8200"
  tls_disable = true
}

default_lease_ttl = "168h"
max_lease_ttl     = "0h"
ui                = true
log_level         = "debug"
