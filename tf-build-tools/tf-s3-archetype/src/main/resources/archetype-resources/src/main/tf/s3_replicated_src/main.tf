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

data "template_file" "assume_role_policy" {
  template = "${file("${path.module}/policies/assume-role-policy.json")}"
}

data "template_file" "replication_policy" {
  template = "${file("${path.module}/policies/replication-policy.json")}"
  vars {
    source_bucket_arn = "${aws_s3_bucket.source_bucket.arn}"
    destination_bucket_arn = "${var.destination_bucket_arn}"
    source_kms_key_arn = "${aws_kms_key.kmskey.arn}"
    destination_kms_key_arn = "${var.destination_kms_key_arn}"
  }
}

resource "aws_iam_role" "replication" {
  name = "tf-iam-role-${local.bucket_name}-replication"
  assume_role_policy = "${data.template_file.assume_role_policy.rendered}"
}

resource "aws_iam_policy" "replication" {
  name = "tf-iam-role-policy-${local.bucket_name}-replication"
  policy = "${data.template_file.replication_policy.rendered}"
}

resource "aws_iam_policy_attachment" "replication" {
  name       = "tf-iam-role-attachment-${local.bucket_name}-replication"
  roles      = ["${aws_iam_role.replication.name}"]
  policy_arn = "${aws_iam_policy.replication.arn}"
}

resource "aws_kms_key" "kmskey" {
  description             = "This key is used to encrypt bucket objects"
  deletion_window_in_days = 10
}

resource "aws_s3_bucket" "source_bucket" {
  region = "${var.region}"
  bucket = "${local.bucket_name}"
  acl    = "private"

  versioning {
    enabled = true
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = "${aws_kms_key.kmskey.arn}"
        sse_algorithm     = "aws:kms"
      }
    }
  }

  replication_configuration {
    role = "${aws_iam_role.replication.arn}"
    "rules" {
      "destination" {
        bucket = "${var.destination_bucket_arn}"
        replica_kms_key_id = "${var.destination_kms_key_arn}"
      }
      "source_selection_criteria" {
        sse_kms_encrypted_objects {
          enabled = true
        }
      }
      status = "Enabled"
    }
  }

  tags = {
    Environment = "${var.environment}"
  }
}
