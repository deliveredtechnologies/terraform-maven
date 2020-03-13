variable "region" {
  description = "region where the bucket will be created or the source region; defaults to us-east-1"
  type = string
  default = "us-east-1"
}

variable "bucket_name" {
  description = "name of the bucket; defaults to a 'bucket-{random id}'"
  type = string
  default = ""
}

variable "environment" {
  description = "value of the 'Environment' tag"
  type = string
  default = "dev"
}

variable "source_kms_key_arn" {
  description = "arn of the source kms key; required if destination_kms_key_arn is supplied"
  type = string
  default = ""
}

variable "destination_kms_key_arn" {
  description = "arn of the destination kms key; required if source_kms_key_arn is supplied"
  type = string
  default = ""
}

variable "destination_bucket_arn" {
  description = "arn of the destination bucket for replication"
  type = string
}
