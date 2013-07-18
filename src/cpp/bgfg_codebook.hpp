#ifndef WUSHUU_BGFG_CODEBOOK_HPP
#define WUSHUU_BGFG_CODEBOOK_HPP


typedef void (*bgfg_cb_t)(int x, int y, int radius);

namespace wushuu {

  class BgFgCodeBook {
    public:
      BgFgCodeBook(bgfg_cb_t bgfg_cb);
      ~BgFgCodeBook();
      void detectVideo(const char* video, const unsigned int frameToLearn = 32);

    private:
      bgfg_cb_t          bgfg_cb_;
  };
}


#endif
