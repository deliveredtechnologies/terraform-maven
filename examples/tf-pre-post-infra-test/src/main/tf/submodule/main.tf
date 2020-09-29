resource "aws_s3_bucket" "bucket3" {
  bucket = "tftest-bucket2-12345"

  versioning {
    enabled = false
  }

  tags = {
    application_id = "cna"
    created_by = "rhutto@deliveredtech.com"
  }
}
