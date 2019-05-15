output "bucket_arns" {
  description = "The arn of the S3 bucket"
  value = "${element(local.bucket_arns, 0)}"
}

output "replicated_bucket_arn" {
  description = "The arn of the S3 bucket that objects are replicated to"
  value = "${element(local.bucket_arns, 1)}"
}
