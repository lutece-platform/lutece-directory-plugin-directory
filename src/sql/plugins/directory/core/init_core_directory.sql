/*==============================================================*/
/*	Init  table core_admin_right								*/
/*==============================================================*/
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url, documentation_url) VALUES ('DIRECTORY_MANAGEMENT','directory.adminFeature.directory_management.name',2,'jsp/admin/plugins/directory/ManageDirectory.jsp','directory.adminFeature.directory_management.description',0,'directory','APPLICATIONS','images/admin/skin/plugins/directory/directory.png', 'jsp/admin/documentation/AdminDocumentation.jsp?doc=admin-directory');

/*==============================================================*/
/*	Init  table  core_admin_role								*/
/*==============================================================*/
INSERT INTO core_admin_role (role_key,role_description) VALUES ('directory_manager','Directory management');

/*==============================================================*/
/*	Init  table  core_admin_role_resource						*/
/*==============================================================*/
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (903,'directory_manager','DIRECTORY_DIRECTORY_TYPE','*','*');
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (904,'directory_manager','DIRECTORY_XSL_FORMAT_TYPE','*','*');


INSERT INTO core_user_right (id_right,id_user) VALUES ('DIRECTORY_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES('DIRECTORY_MANAGEMENT',2);

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('directory_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('directory_manager',2);

--
-- Init  table core_admin_dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('directoryAdminDashboardComponent', 1, 1);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('DIRECTORY', 3, 1);
