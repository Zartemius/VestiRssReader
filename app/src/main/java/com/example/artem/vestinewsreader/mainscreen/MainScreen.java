package com.example.artem.vestinewsreader.mainscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.artem.vestinewsreader.Downloader;
import com.example.artem.vestinewsreader.R;
import com.example.artem.vestinewsreader.articlescreen.ArticleScreen;
import com.example.artem.vestinewsreader.database.AppDataBase;
import com.example.artem.vestinewsreader.database.Article;
import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    private static final String RESOURCE = "http://www.vesti.ru/vesti.rss";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Article> listOfArticles;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        new GetListOfArticlesAsyncTask().execute();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new GetListOfArticlesAsyncTask().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private class GetListOfArticlesAsyncTask extends AsyncTask<Void,Void,Void> {
        private AppDataBase db = AppDataBase.getDatabase(getApplication());
        List<Article> articles = new ArrayList<>();
        Downloader downloader = new Downloader(getApplication());

        @Override
        protected Void doInBackground(Void... voids) {
            db.articleDao().clearListOfArticles();
            downloader.ProcessXml(RESOURCE);
            articles = db.articleDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listOfArticles = articles;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
            mAdapter = new Adapter(getApplication(), listOfArticles);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {

                @Override
                public void onItemClick(final int position) {
                    callActivityArticleScreen(position);
                }
            });
        }
    }

    private void callActivityArticleScreen(int positionInRecyclerview){
        Intent intent = new Intent(this, ArticleScreen.class);
        intent.putExtra("articleId", listOfArticles.get(positionInRecyclerview).getId());
        startActivity(intent);
    }
}
