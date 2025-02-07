FROM openjdk:23-slim-bullseye

RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    pkg-config \
    wget \
    ca-certificates \
    libpng-dev  \
    libjpeg-dev  \
    libtiff-dev  \
    libltdl-dev  \
    libxml2-dev \
    libwebp-dev \
    libheif-dev \
    libaom-dev \
    libperl-dev  \
    ghostscript  \
      && rm -rf /var/lib/apt/lists/*

RUN wget https://download.imagemagick.org/ImageMagick/download/ImageMagick.tar.gz \
    && tar xzf ImageMagick.tar.gz \
    && cd ImageMagick-* \
    && ./configure --with-webp=yes --with-heic=yes --with-avif=yes \
    && make \
    && make install \
    && ldconfig \
    && cd .. \
    && rm -rf ImageMagick*

CMD ["magick"]
