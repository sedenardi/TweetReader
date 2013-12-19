package com.sandersdenardi.tweetreader;

import java.sql.Date;

public class Tweet {
    public long id;
    public boolean retweeted;
    public Date created_at;
    public String text;
    public User user;
}
