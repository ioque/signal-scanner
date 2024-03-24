basename = "calc-server"

pool_path = "/var/lib/libvirt/"

image = {
  name = "ubuntu-focal"
  url  = "https://cloud-images.ubuntu.com/focal/current/focal-server-cloudimg-amd64-disk-kvm.img"
}

vm = {
  bridge = "br0"
  cpu    = 8
  disk   = 50 * 1024 * 1024 * 1024
  ram    = 16 * 1024
}

domains = [
  {
    name = "test-calc-server"
    cpu  = 10
    ram  = 32 * 1024
    disk = 50 * 1024 * 1024 * 1024
    mac = "52:54:00:36:df:b4"
  },
  {
    name = "prod-calc-server"
    cpu  = 30
    ram  = 64 * 1024
    disk = 1000 * 1024 * 1024 * 1024
    mac = "52:54:00:be:53:f4"
  }
]