package org.kodluyoruz.milliyet_watchface.api.model;

import java.util.List;

public class SNODataClass {

    /**
     * data : {"weight":24,"source":{"id":"606","name":"The Times","url":""},"updatedDateFormatted":"2018-07-03 09:44:09","url":"https://www.thetimes.co.uk/article/what-s-new-pussycat-it-s-time-for-your-swim-turkish-van-cats-get-their-own-pool-k5jhgmvh5","tags":[{"id":1,"name":"Öne Çıkanlar","followingType":"Auto","isPrimary":false},{"id":613,"name":"Kahvelik","followingType":"Manual","isPrimary":false},{"id":399,"name":"Hayvanlar Alemi","followingType":"Manual","isPrimary":true},{"id":143,"name":"Aktüel","followingType":"Manual","isPrimary":false},{"id":73,"name":"İyi Haberler","followingType":"Manual","isPrimary":false}],"presentationType":"Image","primaryTag":{"id":399,"name":"Hayvanlar Alemi","followingType":"Manual"},"publishedDateFormatted":"2018-07-03 05:58:18","id":39215,"status":1,"publishedDate":1530597498000,"content":"<p>\u2022 Times gazetesi, Van Yüzüncü Yıl Üniversitesi bünyesindeki Kedievi Villası'nın içine yüzme havuzları eklenerek yenilenmesini sayfalarına taşıdı.<\/p>\r\n\r\n<p>\u2022 Haberde, Üniversite bünyesindeki Van Kedisi Araştırma Merkezi'nde yer alan ve 350 kedinin yaşadığı Kedievi Villası'nın bir iş adamının desteğiyle yenilendiği, artık içinde bir havuzun da bulunduğu aktarılıyor.<\/p>\r\n\r\n<p>\u2022 Gazete, kedilerin genelde yüzmeyi sevmediklerini ancak bunun Van kedileri için geçerli olmadığını belirtiyor.<\/p>\r\n\r\n<p>\u2022 Araştırma Merkezi Müdürü Abdullah Kaya, \"Şimdi yavru kedilere nasıl yüzüleceğini öğretebileceğiz\" demiş.<\/p>\r\n\r\n<p>\u2022 Times, Van kedilerinin beyaz tüyleri ve renkli gözlerinden genelde sadece kendi türleriyle çiftleşmelerine kadar farklı özellikleri olduğunu yazıyor.<\/p>\r\n\r\n<p>\u2022 Gazete, son dönemde Türkiye'de kamuoyunun hayvanlarla ilgili şiddet olayları nedeniyle çok öfkeli olduğunu, son olarak bir yavru köpeğin dört ayağının kesildiğini ve Cumhurbaşkanı Tayyip Erdoğan'ın konuyla ilgili \"Hayvanlar bir mal değil candır\" tweetini attığını belirtiyor.<\/p>\r\n\r\n<p>\u2022 Haberde, Türklerin hayvansever bir halk olduğu, son olarak İstanbul'daki kedilerle ilgili belgesel film 'Kedi'nin çekildiğini, kentte insanların kedileri besleyip onlara yardım ettiği de aktarılıyor.<\/p>\r\n","media":[{"contentType":"image/jpeg","format":"Base","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-1,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"},{"contentType":"image/jpeg","format":"Top","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-2,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"}],"title":"Times: Van kedileri için yüzme vakti","seoLink":"times-van-kedileri-icin-yuzme-vakti,39215","images":[{"contentType":"image/jpeg","format":"Base","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-1,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"},{"contentType":"image/jpeg","format":"Top","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-2,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"}],"updatedDate":1530611049000,"summary":"Times gazetesi, Van Yüzüncü Yıl Üniversitesi bünyesindeki Kedievi Villası'nın içine yüzme havuzları eklenerek yenilenmesini sayfalarına taşıdı."}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }


    public static class DataBean {
        /**
         * weight : 24
         * source : {"id":"606","name":"The Times","url":""}
         * updatedDateFormatted : 2018-07-03 09:44:09
         * url : https://www.thetimes.co.uk/article/what-s-new-pussycat-it-s-time-for-your-swim-turkish-van-cats-get-their-own-pool-k5jhgmvh5
         * tags : [{"id":1,"name":"Öne Çıkanlar","followingType":"Auto","isPrimary":false},{"id":613,"name":"Kahvelik","followingType":"Manual","isPrimary":false},{"id":399,"name":"Hayvanlar Alemi","followingType":"Manual","isPrimary":true},{"id":143,"name":"Aktüel","followingType":"Manual","isPrimary":false},{"id":73,"name":"İyi Haberler","followingType":"Manual","isPrimary":false}]
         * presentationType : Image
         * primaryTag : {"id":399,"name":"Hayvanlar Alemi","followingType":"Manual"}
         * publishedDateFormatted : 2018-07-03 05:58:18
         * id : 39215
         * status : 1
         * publishedDate : 1530597498000
         * content : <p>• Times gazetesi, Van Yüzüncü Yıl Üniversitesi bünyesindeki Kedievi Villası'nın içine yüzme havuzları eklenerek yenilenmesini sayfalarına taşıdı.</p>
         * <p>
         * <p>• Haberde, Üniversite bünyesindeki Van Kedisi Araştırma Merkezi'nde yer alan ve 350 kedinin yaşadığı Kedievi Villası'nın bir iş adamının desteğiyle yenilendiği, artık içinde bir havuzun da bulunduğu aktarılıyor.</p>
         * <p>
         * <p>• Gazete, kedilerin genelde yüzmeyi sevmediklerini ancak bunun Van kedileri için geçerli olmadığını belirtiyor.</p>
         * <p>
         * <p>• Araştırma Merkezi Müdürü Abdullah Kaya, "Şimdi yavru kedilere nasıl yüzüleceğini öğretebileceğiz" demiş.</p>
         * <p>
         * <p>• Times, Van kedilerinin beyaz tüyleri ve renkli gözlerinden genelde sadece kendi türleriyle çiftleşmelerine kadar farklı özellikleri olduğunu yazıyor.</p>
         * <p>
         * <p>• Gazete, son dönemde Türkiye'de kamuoyunun hayvanlarla ilgili şiddet olayları nedeniyle çok öfkeli olduğunu, son olarak bir yavru köpeğin dört ayağının kesildiğini ve Cumhurbaşkanı Tayyip Erdoğan'ın konuyla ilgili "Hayvanlar bir mal değil candır" tweetini attığını belirtiyor.</p>
         * <p>
         * <p>• Haberde, Türklerin hayvansever bir halk olduğu, son olarak İstanbul'daki kedilerle ilgili belgesel film 'Kedi'nin çekildiğini, kentte insanların kedileri besleyip onlara yardım ettiği de aktarılıyor.</p>
         * <p>
         * media : [{"contentType":"image/jpeg","format":"Base","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-1,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"},{"contentType":"image/jpeg","format":"Top","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-2,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"}]
         * title : Times: Van kedileri için yüzme vakti
         * seoLink : times-van-kedileri-icin-yuzme-vakti,39215
         * images : [{"contentType":"image/jpeg","format":"Base","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-1,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"},{"contentType":"image/jpeg","format":"Top","name":"2018/07/03/times-van-kedileri-icin-yuzme-vakti-2,39215.jpg","baseUrl":"https://image-cdn.sonraneoldu.com/images/"}]
         * updatedDate : 1530611049000
         * summary : Times gazetesi, Van Yüzüncü Yıl Üniversitesi bünyesindeki Kedievi Villası'nın içine yüzme havuzları eklenerek yenilenmesini sayfalarına taşıdı.
         */

        private int weight;
        private SourceBean source;
        private String updatedDateFormatted;
        private String url;
        private String presentationType;
        private PrimaryTagBean primaryTag;
        private String publishedDateFormatted;
        private int id;
        private int status;
        private long publishedDate;
        private String content;
        private String title;
        private String seoLink;
        private long updatedDate;
        private String summary;
        private List<TagsBean> tags;
        private List<MediaBean> media;
        private List<ImagesBean> images;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public SourceBean getSource() {
            return source;
        }

        public void setSource(SourceBean source) {
            this.source = source;
        }

        public String getUpdatedDateFormatted() {
            return updatedDateFormatted;
        }

        public void setUpdatedDateFormatted(String updatedDateFormatted) {
            this.updatedDateFormatted = updatedDateFormatted;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPresentationType() {
            return presentationType;
        }

        public void setPresentationType(String presentationType) {
            this.presentationType = presentationType;
        }

        public PrimaryTagBean getPrimaryTag() {
            return primaryTag;
        }

        public void setPrimaryTag(PrimaryTagBean primaryTag) {
            this.primaryTag = primaryTag;
        }

        public String getPublishedDateFormatted() {
            return publishedDateFormatted;
        }

        public void setPublishedDateFormatted(String publishedDateFormatted) {
            this.publishedDateFormatted = publishedDateFormatted;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(long publishedDate) {
            this.publishedDate = publishedDate;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSeoLink() {
            return seoLink;
        }

        public void setSeoLink(String seoLink) {
            this.seoLink = seoLink;
        }

        public long getUpdatedDate() {
            return updatedDate;
        }

        public void setUpdatedDate(long updatedDate) {
            this.updatedDate = updatedDate;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<TagsBean> getTags() {
            return tags;
        }

        public void setTags(List<TagsBean> tags) {
            this.tags = tags;
        }

        public List<MediaBean> getMedia() {
            return media;
        }

        public void setMedia(List<MediaBean> media) {
            this.media = media;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public static class SourceBean {
            /**
             * id : 606
             * name : The Times
             * url :
             */

            private String id;
            private String name;
            private String url;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class PrimaryTagBean {
            /**
             * id : 399
             * name : Hayvanlar Alemi
             * followingType : Manual
             */

            private int id;
            private String name;
            private String followingType;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFollowingType() {
                return followingType;
            }

            public void setFollowingType(String followingType) {
                this.followingType = followingType;
            }
        }

        public static class TagsBean {
            /**
             * id : 1
             * name : Öne Çıkanlar
             * followingType : Auto
             * isPrimary : false
             */

            private int id;
            private String name;
            private String followingType;
            private boolean isPrimary;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFollowingType() {
                return followingType;
            }

            public void setFollowingType(String followingType) {
                this.followingType = followingType;
            }

            public boolean isIsPrimary() {
                return isPrimary;
            }

            public void setIsPrimary(boolean isPrimary) {
                this.isPrimary = isPrimary;
            }
        }

        public static class MediaBean {
            /**
             * contentType : image/jpeg
             * format : Base
             * name : 2018/07/03/times-van-kedileri-icin-yuzme-vakti-1,39215.jpg
             * baseUrl : https://image-cdn.sonraneoldu.com/images/
             */

            private String contentType;
            private String format;
            private String name;
            private String baseUrl;

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
                this.contentType = contentType;
            }

            public String getFormat() {
                return format;
            }

            public void setFormat(String format) {
                this.format = format;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBaseUrl() {
                return baseUrl;
            }

            public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
            }
        }

        public static class ImagesBean {
            /**
             * contentType : image/jpeg
             * format : Base
             * name : 2018/07/03/times-van-kedileri-icin-yuzme-vakti-1,39215.jpg
             * baseUrl : https://image-cdn.sonraneoldu.com/images/
             */

            private String contentType;
            private String format;
            private String name;
            private String baseUrl;

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
                this.contentType = contentType;
            }

            public String getFormat() {
                return format;
            }

            public void setFormat(String format) {
                this.format = format;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBaseUrl() {
                return baseUrl;
            }

            public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
            }
        }
    }
}
