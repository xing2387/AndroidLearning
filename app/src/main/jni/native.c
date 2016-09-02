#include "native.h"

#ifndef loge
#define loge(...) __android_log_print(ANDROID_LOG_ERROR,"@",__VA_ARGS__)
#endif
#ifndef logd
#define logd(...) __android_log_print(ANDROID_LOG_DEBUG,"@",__VA_ARGS__)
#endif


#define RGBA_A(p) (((p) & 0xFF000000) >> 24)
#define RGBA_R(p) (((p) & 0x00FF0000) >> 16)
#define RGBA_G(p) (((p) & 0x0000FF00) >>  8)
#define RGBA_B(p)  ((p) & 0x000000FF)
#define MAKE_RGBA(r, g, b, a) (((a) << 24) | ((r) << 16) | ((g) << 8) | (b))

enum FILTER_TYPE {
    FILTER_TYPE_GRAYSCALE = 0,
    FILTER_TYPE_BORW = 1,
    FILTER_TYPE_NEGATIVE = 2,
    FILTER_TYPE_ANAGLYPH = 3
};

JNIEXPORT jstring JNICALL
Java_com_example_xing_androidlearning_natives_JniEntry_getString(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "hello");
}

JNIEXPORT void JNICALL
Java_com_example_xing_androidlearning_natives_JniEntry_processBitmap(JNIEnv *env, jclass instance,
                                                                     jobject jBitmap) {
    logd("JniEntry_processBitmap\n");
    if (jBitmap == NULL) {
        logd("bitmap is null\n");
        return;
    }

    AndroidBitmapInfo androidBitmapInfo;
    memset(&androidBitmapInfo, 0, sizeof(androidBitmapInfo));
    AndroidBitmap_getInfo(env, jBitmap, &androidBitmapInfo);
    int height = androidBitmapInfo.height;
    int width = androidBitmapInfo.width;

    if (height <= 0 || width <= 0) {
        loge("pic is empty\n");
        return;
    }

    if (androidBitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        loge("unsuport bitmap fotmat, only accept rgba8888\n");
        return;
    }

    void *pixels = NULL;
    int result = AndroidBitmap_lockPixels(env, jBitmap, &pixels);
    if (pixels == NULL) {
        loge("fail to lock bitmap: %d\n", result);
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/io/IOException"), "fail to open bitmap");
        return;
    }
    filterBorW(&pixels, height, width);
    AndroidBitmap_unlockPixels(env, jBitmap);
}


JNIEXPORT void JNICALL
Java_com_example_xing_androidlearning_natives_JniEntry_bitmapFilter(JNIEnv *env, jclass type,
                                                                    jobject bitmap,
                                                                    jint filterType) {
    switch (filterType) {
        case FILTER_TYPE_BORW:
            logd("FILTER_TYPE_BORW");
            processBitmap(env, type, bitmap, filterBorW);
            break;
        case FILTER_TYPE_GRAYSCALE:
            logd("FILTER_TYPE_GRAYSCALE");
            processBitmap(env, type, bitmap, filterGrayscale);
            break;
        case FILTER_TYPE_NEGATIVE:
            processBitmap(env, type, bitmap, filterNagetive);
            break;
        case FILTER_TYPE_ANAGLYPH:
            processBitmap(env, type, bitmap, filterAnaglyph);

            break;
    }

}

void processBitmap(JNIEnv *env, jclass instance, jobject jBitmap,
                   void (*filter)(void **pixels, int height, int width)) {
    logd("JniEntry_processBitmap\n");
    if (jBitmap == NULL) {
        logd("bitmap is null\n");
        return;
    }

    AndroidBitmapInfo androidBitmapInfo;
    memset(&androidBitmapInfo, 0, sizeof(androidBitmapInfo));
    AndroidBitmap_getInfo(env, jBitmap, &androidBitmapInfo);
    int height = androidBitmapInfo.height;
    int width = androidBitmapInfo.width;

    if (height <= 0 || width <= 0) {
        loge("pic is empty\n");
        return;
    }

    if (androidBitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        loge("unsuport bitmap fotmat, only accept rgba8888\n");
        return;
    }

    void *pixels = NULL;
    int result = AndroidBitmap_lockPixels(env, jBitmap, &pixels);
    if (pixels == NULL) {
        loge("fail to lock bitmap: %d\n", result);
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/io/IOException"), "fail to open bitmap");
        return;
    }

//    filterBorW(&pixels, height, width);
    (*filter)(&pixels, height, width);

    AndroidBitmap_unlockPixels(env, jBitmap);
}

void filterGrayscale(void **pixels, int height, int width) {
    int x = 0, y = 0;
    // From top to bottom
    for (y = 0; y < height; ++y) {
        // From left to right
        for (x = 0; x < width; ++x) {
            int a = 0, r = 0, g = 0, b = 0;
            void *pixel = NULL;
            // Get each pixel by format
            pixel = ((uint32_t *) *pixels) + y * width + x;
            uint32_t v = *(uint32_t *) pixel;
            a = RGBA_A(v);
            r = RGBA_R(v);
            g = RGBA_G(v);
            b = RGBA_B(v);

            // Grayscale
            int gray = (r * 30 + g * 59 + b * 11) >> 7;

            // Write the pixel back
            *((uint32_t **) pixel) = MAKE_RGBA(gray, gray, gray, a);
        }
    }
}

void filterBorW(void **pixels, int height, int width) {
    int x = 0, y = 0;
    // From top to bottom
    for (y = 0; y < height; ++y) {
        // From left to right
        for (x = 0; x < width; ++x) {
            int a = 0, r = 0, g = 0, b = 0;
            void *pixel = NULL;
            // Get each pixel by format
            pixel = ((uint32_t *) *pixels) + y * width + x;
            uint32_t v = *(uint32_t *) pixel;
            a = RGBA_A(v);
            r = RGBA_R(v);
            g = RGBA_G(v);
            b = RGBA_B(v);

            // Grayscale
            int gray = ((r + g + b) >> 2);  //原本要除以3再判断大于100， 现在右移动两位是除以4在判断大于75
            gray = ((gray > 75) ? 255 : 0);

            // Write the pixel back
            *((uint32_t **) pixel) = MAKE_RGBA(gray, gray, gray, a);
        }
    }
}

void filterNagetive(void **pixels, int height, int width) {
    int x = 0, y = 0;
    // From top to bottom
    for (y = 0; y < height; ++y) {
        // From left to right
        for (x = 0; x < width; ++x) {
            int a = 0, r = 0, g = 0, b = 0;
            void *pixel = NULL;
            // Get each pixel by format
            pixel = ((uint32_t *) *pixels) + y * width + x;
            uint32_t v = *(uint32_t *) pixel;
            a = RGBA_A(v);
            r = RGBA_R(v);
            g = RGBA_G(v);
            b = RGBA_B(v);

//            int gray = ((r + g + b) >> 2);  //原本要除以3再判断大于100， 现在右移动两位是除以4在判断大于75
//            gray = ((gray > 75) ? 255 : 0);

            // Write the pixel back
            *((uint32_t **) pixel) = MAKE_RGBA(255 - r, 255 - g, 255 - b, a);
        }
    }
}

void filterAnaglyph(void **pixels, int height, int width) {
    int x = 0, y = 0;
    // From top to bottom
    for (y = 0; y < height; ++y) {
        // From left to right
        for (x = 0; x < width; ++x) {
            int a = 0, r = 0, g = 0, b = 0;
            void *pixel = NULL;
            void *prePixel = NULL;
            // Get each pixel by format
            pixel = ((uint32_t *) *pixels) + y * width + x;
            prePixel = ((uint32_t *) *pixels) + y * width + x + 1;
            uint32_t v = *(uint32_t *) pixel;
            uint32_t pv = *(uint32_t *) prePixel;
            a = RGBA_A(v) - RGBA_A(pv) + 128;
            r = RGBA_R(v) - RGBA_R(pv) + 128;
            g = RGBA_G(v) - RGBA_G(pv) + 128;
            b = RGBA_B(v) - RGBA_B(pv) + 128;

//            int gray = ((r + g + b) >> 2);  //原本要除以3再判断大于100， 现在右移动两位是除以4在判断大于75
//            gray = ((gray > 75) ? 255 : 0);

            // Write the pixel back
            *((uint32_t **) pixel) = MAKE_RGBA(r, g, b, a);
        }
    }
}
