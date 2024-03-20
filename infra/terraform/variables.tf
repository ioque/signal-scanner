# Префикс для создаваемых объектов
variable "basename" {
  type    = string
  default = "calc-server"
}

variable "prefix" {
  type    = string
  default = "prod"
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