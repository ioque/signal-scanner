# Префикс для создаваемых объектов
variable "basename" {
  type    = string
  default = "calc-server"
}

# Путь, где будет хранится пул проекта
variable "pool_path" {
  type    = string
  default = "/var/lib/libvirt/"
}

# Параметры облачного образа
variable "image" {
  type = object({
    name = string
    url  = string
  })
}

# Параметры виртуальной машины
variable "vm" {
  type = object({
    cpu    = number
    ram    = number
    disk   = number
    bridge = string
  })
}

variable "domains" {
  description = "List of VMs with specified parameters"
  type = list(object({
    name = string,
    cpu  = number,
    ram  = number,
    disk = number
  }))
}