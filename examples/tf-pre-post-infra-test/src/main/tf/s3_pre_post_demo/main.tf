resource "random_id" "id" {
  byte_length = 2
}
provider "aws" {
  region = "us-east-1"
}
resource "aws_s3_bucket" "bucket1" {
  bucket = "tftest-bucket1-${random_id.id.dec}"

  versioning {
    enabled = true
  }

  tags = {
    application_id = "cna"
    stack_name = "stacked"
    created_by = "rhutto@deliveredtech.com"
  }
}

resource "aws_s3_bucket" "bucket2" {
  bucket = "tftest-bucket2-${random_id.id.dec}"

  versioning {
    enabled = false
  }

  tags = {
    application_id = "cna"
    created_by = "rhutto@deliveredtech.com"
  }
}

module "s3module" {
  source = "../submodule"
}

output "bucket_name" {
  value = [aws_s3_bucket.bucket1.bucket, aws_s3_bucket.bucket2.bucket]
}
