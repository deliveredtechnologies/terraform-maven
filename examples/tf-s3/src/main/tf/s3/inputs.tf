variable "region" {
  description = "region where the bucket will be created or the source region; defaults to us-east-1"
  type = "string"
  default = "us-east-1"
}

variable "replication_region" {
  description = "region where the bucket will be repliced to (destination), if applicable; defaults to us-west-1"
  type = "string"
  default = "us-west-1"
}

variable "bucket_name" {
  description = "name of the bucket; defaults to a 'bucket-{random id}'"
  type = "string"
  default = ""
}

variable "environment" {
  description = "value of the 'Environment' tag"
  type = "string"
  default = "dev"
}

variable "is_versioned" {
  description = "true if versioning is to be enabled, otherwise false; defaults to false"
  type = "string"
  default = false
}

variable "is_replicated" {
  description = "true if replication is to be enabled, otherwise false; defaults to false"
  type = "string"
  default = false
}
