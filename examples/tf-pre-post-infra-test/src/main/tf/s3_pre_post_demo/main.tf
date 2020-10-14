resource "random_id" "id" {
  byte_length = 2
}
provider "aws" {
  region = "us-east-1"
}
resource "aws_s3_bucket" "bucket" {
  bucket = "tftest-bucket-${random_id.id.dec}"

  versioning {
    enabled = true
  }

  tags = {
    application_id = "cna"
    stack_name = "stacked"
    created_by = "rhutto@deliveredtech.com"
  }
}

output "bucket_name" {
  value = aws_s3_bucket.bucket.bucket
}
