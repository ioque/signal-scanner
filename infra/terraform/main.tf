resource "libvirt_pool" "pool" {
  name = "${var.basename}-pool"
  type = "dir"
  path = "${var.pool_path}${var.basename}-pool"
}

resource "libvirt_volume" "image" {
  name   = var.image.name
  format = "qcow2"
  pool   = libvirt_pool.pool.name
  source = var.image.url
}

resource "libvirt_volume" "root" {
  name           = "${var.prefix}-${var.basename}-root"
  pool           = libvirt_pool.pool.name
  base_volume_id = libvirt_volume.image.id
  size           = var.vm.disk
}

resource "libvirt_domain" "vm" {
  name   = "${var.prefix}-${var.basename}"
  memory = var.vm.ram
  vcpu   = var.vm.cpu
  qemu_agent = true
  autostart  = true

  cloudinit = libvirt_cloudinit_disk.commoninit.id

  network_interface {
    bridge         = var.vm.bridge
    wait_for_lease = true
  }

  disk {
    volume_id = libvirt_volume.root.id
  }
}

data "template_file" "user_data" {
  template = file("${path.module}/cloud_init.cfg")
}

data "template_file" "network_config" {
  template = file("${path.module}/network_config.cfg")
}

resource "libvirt_cloudinit_disk" "commoninit" {
  name           = "commoninit.iso"
  pool           = libvirt_pool.pool.name
  user_data      = data.template_file.user_data.rendered
  network_config = data.template_file.network_config.rendered
}