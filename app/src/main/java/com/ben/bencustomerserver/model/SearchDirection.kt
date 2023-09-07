package com.ben.bencustomerserver.model;

public enum SearchDirection {
        /**
         *\~chinese
         * 按照消息中的 Unix 时间戳的逆序搜索。
         *
         *\~english
         * Messages are retrieved in the descending order of the Unix timestamp included in them.
         */
        UP,     

        /**
         *\~chinese
         *  按照消息中的时间戳的正序搜索。
         *
         *\~english
         * Messages are retrieved in the ascending order of the Unix timestamp included in them.
         */
        DOWN    
    }