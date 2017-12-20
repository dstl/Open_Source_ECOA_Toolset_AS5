/* File ecoaByteswap.h */


#ifdef LITTLE_ENDIAN
#define bswap16(x) \
     ((((x) >> 8) & 0xff) | (((x) & 0xff) << 8))

#define bswap32(x) \
     ((((x) & 0xff000000) >> 24) | (((x) & 0x00ff0000) >>  8) | \
      (((x) & 0x0000ff00) <<  8) | (((x) & 0x000000ff) << 24))
#else
#define bswap16(x) (x)
#define bswap32(x) (x)
#endif
