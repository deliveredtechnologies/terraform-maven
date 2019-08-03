output "bucket_arn" {
  description = "bucket arn"
  value = "${aws_s3_bucket.bucket.arn}"
}

output "kms_key_arn" {
  description = "kms key arn for the bucket encryption"
  value = "${aws_kms_key.kmskey.arn}"
}
