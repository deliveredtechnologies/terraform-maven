output "bucket_arns" {
  description = "a list of bucket arns: [bucket_arn] for non-replicated; [source_arn, destination_arn] for replicated"
  value = "${var.is_replicated ? "${join("", aws_s3_bucket.source_bucket.*.arn)},${join("", aws_s3_bucket.destination_bucket.*.arn)}" : join("", aws_s3_bucket.bucket.*.arn)}"
}
