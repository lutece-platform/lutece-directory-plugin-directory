--
-- Init  table core_admin_dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('directoryAdminDashboardComponent', 1, 1);

--
-- Update table core_admin_right
--
UPDATE core_admin_right SET admin_url = 'jsp/admin/plugins/directory/ManageDirectory.jsp' WHERE id_right = 'DIRECTORY_MANAGEMENT';
