output "vm_name" {
  value       = libvirt_domain.vm.name
  description = "VM name"
}

output "vm_ip" {
  value       = libvirt_domain.vm.network_interface[0].addresses.0
  description = "Interface IPs"
}