package com.example.artem.vestinewsreader.articlescreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.artem.vestinewsreader.R;
import com.example.artem.vestinewsreader.database.AppDataBase;
import com.example.artem.vestinewsreader.database.Article;
import com.squareup.picasso.Picasso;

public class ArticleScreen extends AppCompatActivity {

    private TextView title;
    private ImageView picture;
    private TextView text;
    private int receivedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        receivedId = intent.getIntExtra("articleId",1);

        title = findViewById(R.id.article_screen_title);
        picture = findViewById(R.id.article_screen_picture);
        text = findViewById(R.id.article_screen_text);

        new GetArticleAsyncTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetArticleAsyncTask extends AsyncTask<Void,Void,Void>{

        private AppDataBase db = AppDataBase.getDatabase(getApplication());
        Article article = null;

        @Override
        protected Void doInBackground(Void... voids) {
            article = db.articleDao().findById(receivedId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            title.setText(article.getTitle());
            if(article.getPicture() != null) {
                Picasso.with(getApplication())
                        .load(article.getPicture())
                        .into(picture);
            }
            text.setText(article.getText());
        }
    }
}
