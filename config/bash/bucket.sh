#添加策略
mc admin policy add local getonly getonly.json
#添加用户
mc admin user add local test 123456
#添加策略到用户
mc admin policy set local getonly user=test
#添加策略到用户组
mc admin policy set local getonly group=test_group
#禁用用户 newuser。
mc admin user disable myminio newuser
#禁用组 newgroup。
mc admin group disable myminio newgroup
#列出所有用户或组
mc admin user list minio别名
mc admin group list minio别名
#删除策略
mc admin policy remove minio别名 listbucket

mc admin policy add local gettwo gettwo.json
mc admin policy set local gettwo group=test_group

#添加策略
mc admin policy add local getone getone.json
mc admin policy set local getone user=test

#删除策略
mc admin policy set local '' user=test
mc admin policy remove local getone
