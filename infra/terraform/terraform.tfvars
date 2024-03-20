basename = "calc-server"
prefix = "test"

pool_path = "/var/lib/libvirt/"

image = {
  name = "ubuntu-focal"
  url  = "https://cloud-images.ubuntu.com/focal/current/focal-server-cloudimg-amd64-disk-kvm.img"
}

vm = {
  bridge = "virbr0"
  cpu    = 8
  disk   = 50 * 1024 * 1024 * 1024
  ram    = 16 * 1024
}