provider "aws" {
  region = "${var.region}"
}

provider "aws" {
  alias = "replication"
  region = "${var.replication_region}"
}

resource "random_id" "bucket_name" {
  prefix = "bucket-"
  byte_length = 8
}

locals {
  bucket_name = "${length(var.bucket_name) == 0 ? random_id.bucket_name.hex : var.bucket_name}"
}

data "template_file" "assume_role_policy" {
  template = "${file("policies/assume-role-policy.json")}"
}

data "template_file" "replication_policy" {
  template = "${file("policies/replication-policy.json")}"
  vars {
    bucket_arn = "${aws_s3_bucket.bucket.arn}"
  }
}

resource "aws_iam_role" "replication" {
  count = "${var.is_replicated ? 1 : 0}"
  provider = "aws.replication"
  name = "tf-iam-role-${local.bucket_name}-replication"
  assume_role_policy = "${data.template_file.assume_role_policy.rendered}"
}

resource "aws_iam_policy" "replication" {
  count = "${var.is_replicated ? 1 : 0}"
  provider = "aws.replication"
  name = "tf-iam-role-policy-${local.bucket_name}-replication"
  policy = "${data.template_file.replication_policy.rendered}"
}

resource "aws_iam_policy_attachment" "replication" {
  count = "${var.is_replicated ? 1 : 0}"
  provider = "aws.replication"
  name       = "tf-iam-role-attachment-${local.bucket_name}-replication"
  roles      = ["${aws_iam_role.replication.name}"]
  policy_arn = "${aws_iam_policy.replication.arn}"
}

resource "aws_kms_key" "kmskey" {
  description             = "This key is used to encrypt bucket objects"
  deletion_window_in_days = 10
}

resource "aws_kms_key" "replication_kmskey" {
  count = "${var.is_replicated ? 1 : 0}"
  provider = "aws.replication"
  description             = "This key is used to encrypt replicated bucket objects"
  deletion_window_in_days = 10
}

resource "aws_s3_bucket" "replication_bucket" {
  count = "${var.is_replicated ? 1 : 0}"
  provider = "aws.replication"
  bucket = "${var.bucket_name}-replica"
  region = "${var.replication_region}"

  versioning {
    enabled = true
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${aws_kms_key.replication_kmskey.arn}"
        sse_algorithm     = "aws:kms"
      }
    }
  }
}

resource "aws_s3_bucket" "source_bucket" {
  count = "${var.is_replicated ? 1 : 0}"
  region = "${var.region}"
  bucket = "${local.bucket_name}"
  acl    = "private"

  versioning {
    enabled = "${var.is_replicated ? true : var.is_versioned}"
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
    Environment = "${var.environment}"
  }
}

resource "aws_s3_bucket" "bucket" {
  count = "${var.is_replicated ? 0 : 1}"
  region = "${var.region}"
  bucket = "${local.bucket_name}"
  acl    = "private"

  versioning {
    enabled = "${var.is_replicated ? true : var.is_versioned}"
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
    Environment = "${var.environment}"
  }
}
