#ifndef WUSHUU_BGFG_CODEBOOK_HPP
#define WUSHUU_BGFG_CODEBOOK_HPP

#include "wsnative_export.h"

typedef void (*bgfg_cb_t)(int x, int y, int w, int h);

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

extern "C" {

WSNATIVE_EXPORT wushuu::BgFgCodeBook* bgfgcb_create(bgfg_cb_t bgfg_cb);
WSNATIVE_EXPORT void bgfgcb_destroy(wushuu::BgFgCodeBook* bf);
WSNATIVE_EXPORT void bgfgcb_detect_video(wushuu::BgFgCodeBook* bf, const char* videoFile);

}

#endif
