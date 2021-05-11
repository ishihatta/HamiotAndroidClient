# HamiotAndroidClient
## 概要
これは [Hyperledger Iroha](https://github.com/hyperledger/iroha) を用いて構築した暗号資産（仮想通貨）システム HamIOT （ハミオット）の Android 用クライアントアプリです。
HamIOT はブロックチェーンの学習の一貫として開発した、小規模仮想通貨システムのプロトタイプです。学習目的のため実稼働に耐え得る作りにはなっていません（特にセキュリティ面）のでご注意ください。

## システム構成
- [Iroha node](https://github.com/hyperledger/iroha)
- [HamIOT Server (Cloud functions for Firebase)](https://github.com/ishihatta/HamiotFirebase)
- Android 用クライアントアプリ（このリポジトリ）

## このアプリでできること
- アカウントの作成
  - ユーザ名（表示名）の登録
  - キーペアの自動生成
- アカウントのバックアップ/リストア
- 残高の表示
- QRコードによる送金
  - 受け取り側の端末で QR コードを表示し送り側の端末がそれをスキャンする方式
  - 送金成功時のプッシュ通知
- 取り引き履歴の表示
- ログアウト

## システム全体のアーキテクチャ（概要）
- 基本的に Iroha の機能をそのまま使う
- ユーザに紐づく情報（ユーザ名と FCM トークン）は Iroha の AccountDetail に保存する。
- キーペアの生成はクライアント側で行いローカルに保存する。
- アカウントの作成は Cloud Functions を介して行う。
- 残高や履歴の取得はクライアントが Iroha node に直接アクセスする。
- 送金はトランザクション情報をクライアントで生成および署名し、それを Cloud Functions に渡す。Cloud Functions はそのトランザクションを Iroha node に送る。成功したら送金側と受領側両方にプッシュ通知を送る。

## このアプリのアーキテクチャ
基本的に MVVM で構築しています。また以下の技術を利用しています。

- Kotlin coroutine
- LiveData
- Coroutine Flow (SharedFlow)
- Navigation with safe args
- Data binding
- Dagger Hilt

## デモ
エミュレータで送金操作しているときの動画です。送金完了と同時に受け取り側の端末にも通知が行き、自動的に残高が変化することが分かります。

（エミュレータの問題でQRコードのスキャンに時間がかかっていますが実機だとこんなにかかりません）

![送金時の動画](https://user-images.githubusercontent.com/40629744/117840640-50d7a080-b2b7-11eb-8755-11df12d8c22f.gif)
