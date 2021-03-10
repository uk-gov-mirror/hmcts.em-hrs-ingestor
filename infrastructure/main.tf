provider "azurerm" {
  features {}
}

locals {
  app_full_name = "${var.product}-${var.component}"
  tags = "${merge(
      var.common_tags,
      map(
        "Team Contact", var.team_contact,
        "Destroy Me", var.destroy_me
      )
    )}"
}

resource "azurerm_resource_group" "rg" {
  name = "${local.app_full_name}-${var.env}"
  location = var.location
  tags = var.common_tags
}

module "key-vault" {
  source = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product = local.app_full_name
  env = var.env
  tenant_id = var.tenant_id
  object_id = var.jenkins_AAD_objectId
  resource_group_name = azurerm_resource_group.rg.name
  product_group_object_id = "5d9cd025-a293-4b97-a0e5-6f43efce02c0"
  common_tags = var.common_tags
  managed_identity_object_id = data.azurerm_user_assigned_identity.em-shared-identity.principal_id
}

data "azurerm_user_assigned_identity" "em-shared-identity" {
  name = "rpa-${var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}
