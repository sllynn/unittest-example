FROM ubuntu:16.04

ENV SCALA_VERSION 2.11.12
ENV SBT_VERSION 1.3.2
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ENV PATH /opt/conda/bin:$PATH

# Install OS basics
RUN apt-get update --fix-missing && \
    apt-get install  --yes \
      openjdk-8-jdk \
      iproute2 \
      bash \
      sudo \
      coreutils \
      procps \
      wget \
      bzip2 \
      ca-certificates \
      libglib2.0-0 \
      libxext6 \
      libsm6 \
      libxrender1 \
      git \
      curl \
    && /var/lib/dpkg/info/ca-certificates-java.postinst configure \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Install sbt
RUN cd /tmp && \
  curl -fsSOL https://piccolo.link/sbt-$SBT_VERSION.tgz && \
  tar -xvzf sbt-$SBT_VERSION.tgz && \
  mv /tmp/sbt /usr/lib && \
  rm /tmp/sbt-$SBT_VERSION.tgz && \
  ln -s /usr/lib/sbt/bin/* /usr/local/bin && \
  sbt sbtVersion

# Install conda
RUN wget --quiet https://repo.anaconda.com/archive/Anaconda3-2019.10-Linux-x86_64.sh -O ~/anaconda.sh && \
    /bin/bash ~/anaconda.sh -b -p /opt/conda && \
    rm ~/anaconda.sh && \
    ln -s /opt/conda/etc/profile.d/conda.sh /etc/profile.d/conda.sh && \
    echo ". /opt/conda/etc/profile.d/conda.sh" >> ~/.bashrc && \
    echo "conda activate base" >> ~/.bashrc && \
    find /opt/conda/ -follow -type f -name '*.a' -delete && \
    find /opt/conda/ -follow -type f -name '*.js.map' -delete && \
    /opt/conda/bin/conda clean -afy

# Create DB Connect
ADD ./conda.yaml ./conda.yaml
RUN conda env create -f conda.yaml
RUN echo "source activate dbconnect" >> ~/.bashrc
RUN echo "export SPARK_LOCAL_HOSTNAME=localhost" >> ~/.bashrc

# Add DB connect credentials update script
COPY ./dbconnect-creds.sh /root/

CMD [ "/bin/bash" ]