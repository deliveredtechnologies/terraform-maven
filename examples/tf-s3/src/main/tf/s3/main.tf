provider "aws" {
  region = var.region
  version = "~> 2.49"
}

resource "random_id" "bucket_name" {
  prefix      = "bucket-"
  byte_length = 8
}

locals {
  bucket_name = length(var.bucket_name) == 0 ? random_id.bucket_name.hex : var.bucket_name
}

resource "aws_s3_bucket" "bucket" {
  bucket        = local.bucket_name
  acl           = "private"
  force_destroy = "true"

  versioning {
    enabled = var.is_versioned
  }

  dynamic "server_side_encryption_configuration" {
  for_each = length(var.kms_key_arn) > 0 ? [1] : []
    content {
      rule {
        apply_server_side_encryption_by_default {
          kms_master_key_id = var.kms_key_arn
          sse_algorithm = "aws:kms"
        }
      }
    }
  }

  tags = {
    environment = var.environment
  }
}

