    package com.genc.e_commerce.util;


    import lombok.Data;

    @Data
    public class Response {
        private Object data;

        public Response(Object data) {
            this.data = data;
        }
    }