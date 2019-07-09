provider "aws" {
  region = "${var.region}"
}

resource "random_id" "bucket_name" {
  prefix = "bucket-"
  byte_length = 8
}

locals {
  bucket_name = "${length(var.bucket_name) == 0 ? random_id.bucket_name.hex : var.bucket_name}"
}

resource "aws_kms_key" "kmskey" {
  description             = "This key is used to encrypt bucket objects"
  deletion_window_in_days = 10
}

resource "aws_s3_bucket" "bucket" {
  bucket = "${local.bucket_name}"
  acl    = "private"

  versioning {
    enabled = "${var.is_versioned}"
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${aws_kms_key.kmskey.arn}"
        sse_algorithm     = "aws:kms"
      }
    }
  }

  tags = {
    environment = "${var.environment}"
  }
}
