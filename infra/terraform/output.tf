output "vms_info" {
  description = "General information about created VMs"
  value = [
    for vm in libvirt_domain.vm : {
      id = vm.name
      ip = vm.network_interface[0].addresses.0
    }
  ]
}