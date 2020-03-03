provider "aws" {
  region = var.region
  version = "~> 2.49"
}

resource "random_id" "bucket_name" {
  prefix = "bucket-"
  byte_length = 8
}

locals {
  bucket_name = length(var.bucket_name) == 0 ? random_id.bucket_name.hex : var.bucket_name
  is_kms_encrypted = length(var.source_kms_key_arn) > 0 && length(var.destination_kms_key_arn) > 0
  replication_policy = templatefile("${path.module}/policies/replication-policy.json",
  {
      source_bucket_arn = aws_s3_bucket.source_bucket.arn
      destination_bucket_arn = var.destination_bucket_arn
  })
  //kms_policy is blank if kms keys are omitted
  kms_policy = local.is_kms_encrypted ? templatefile("${path.module}/policies/kms-policy.json",
  {
    source_kms_key_arn = var.source_kms_key_arn
    destination_kms_key_arn = var.destination_kms_key_arn
  }) : ""
  assume_role_policy = templatefile("${path.module}/policies/assume-role-policy.json", {})
}

resource "aws_iam_role" "replication" {
  name = "tf-iam-role-${local.bucket_name}-replication"
  assume_role_policy = local.assume_role_policy
}

resource "aws_iam_policy" "replication" {
  name = "tf-iam-role-policy-${local.bucket_name}-replication"
  policy = local.replication_policy
}

//only create iam policy for encrypt/decrypt of kms if kms keys were supplied
resource "aws_iam_policy" "kms" {
  name = "tf-iam-role-policy-${local.bucket_name}-replication-kms"
  policy = local.kms_policy
  count = local.is_kms_encrypted ? 1 : 0
}

resource "aws_iam_role_policy_attachment" "replication" {
  role      = aws_iam_role.replication.name
  policy_arn = aws_iam_policy.replication.arn
}

//only attach iam policy for encrypt/decrypt of kms if kms keys were supplied
resource "aws_iam_role_policy_attachment" "kms" {
  role      = aws_iam_role.replication.name
  policy_arn = aws_iam_policy.kms[0].arn
  count = local.is_kms_encrypted ? 1 : 0
}

resource "aws_s3_bucket" "source_bucket" {
  region = var.region
  bucket = local.bucket_name
  acl    = "private"

  versioning {
    enabled = true
  }

  dynamic "server_side_encryption_configuration" {
    for_each = local.is_kms_encrypted ? [1] : []
    content {
      rule {
        apply_server_side_encryption_by_default {
          kms_master_key_id = var.source_kms_key_arn
          sse_algorithm     = "aws:kms"
        }
      }
    }
  }

  replication_configuration {
    role = aws_iam_role.replication.arn
    rules {
      destination {
        bucket = var.destination_bucket_arn
        replica_kms_key_id = var.destination_kms_key_arn
      }
      dynamic "source_selection_criteria" {
        for_each = local.is_kms_encrypted ? [1] : []
        content {
          sse_kms_encrypted_objects {
            enabled = true
          }
        }
      }
      status = "Enabled"
    }
  }

  tags = {
    Environment = var.environment
  }
}
