// MongoDB初期化スクリプト
// データベースの作成と初期ユーザーの設定

// ecsiteデータベースを使用
db = db.getSiblingDB('ecsite');

// コレクションの作成（必要に応じて）
db.createCollection('accounts');
db.createCollection('user_profiles');
db.createCollection('products');
db.createCollection('categories');
db.createCollection('inventories');
db.createCollection('promotions');
db.createCollection('carts');
db.createCollection('orders');
db.createCollection('payments');
db.createCollection('shipments');

// インデックスの作成
db.accounts.createIndex({'authentications.email': 1}, {unique: true});
db.user_profiles.createIndex({'accountId': 1});
db.products.createIndex({'categoryId': 1});
db.carts.createIndex({'accountId': 1});
db.orders.createIndex({'accountId': 1});
db.orders.createIndex({'status': 1});

print('Database initialized successfully');