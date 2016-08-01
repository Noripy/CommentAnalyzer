# CommentAnalyzer
A Tool for NicoNicoLiveBroadcast.

TO-DO List

- 既存のソフト又はAPIを使ってコメント取得
- 取得したコメントを命令系に変える
- 命令系を表すものをBluetoothを使ってデバイスに送信

※1 Markdown記法は[こちら](http://qiita.com/Qiita/items/c686397e4a0f4f11683d)を参考にしています

※2 TO-DO Listの項目を上からやっていきます.


Memo

左上げる:0
左下げる:1
右上げる:2
右下げる:3

制約
・一文字で送信

車
- パワーが0～100％で制御

- 一つのコメントでどのくらいのパワーをup / down?
→来場者数とコメントの数から判断
→→いくつコメントきたらまとめて処理して送るかを考える必要あり

- 32bit環境で作る
- 5桁整数まで拾える
--------------------------------------------------------------------------------------------------------------------------------------
[Amitatatatata必読]開発したコードをmerge完了させるまでのstep

- git add (./<ディレクトリ名>)
- git commit -m "(コメント)"
- git push -u origin master
- gitHub上でpull request
- (NoripyがチェックしてConfirm to merge.)
- git pull https://gitHub.com/Noripy/CommentAnalyzer

--------------------------------------------------------------------------------------------------------------------------------------
その他使えるgitコマンド

- git log → 最新順にコミットのコメントが表示される.⇒git rebaseで戻せる.
- git config -l → git操作で用いるステータスの表示.
- git remote set-url (gitHubアカウントページ) →remote.origin.urlを変更することができる.
