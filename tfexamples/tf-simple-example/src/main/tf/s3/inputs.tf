variable "region" {
  type = "string"
  default = "us-east-1"
}

variable "replication_region" {
  type = "string"
  default = "us-west-1"
}

variable "bucket_name" {
  type = "string"
  default = ""
}

variable "environment" {
  type = "string"
  default = "dev"
}

variable "versioned" {
  type = "string"
  default = false
}

variable "replicated" {
  type = "string"
  default = false
}
