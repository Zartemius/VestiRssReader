package com.example.artem.vestinewsreader.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM articleRepository")
    List<Article> getAll();

    @Query("DELETE FROM articleRepository")
    public void clearListOfArticles();

    @Insert
    public void addArticle(Article news);

    @Query("SELECT * FROM articleRepository where id LIKE :id")
    Article findById(int id);
}
