package me.ghostbear.kumaslash.commands.social

// Goal here is to make the command eventually be able to add/remove/get images, like the logging command, instead of having to PR the bot each time.
class SocialImages {
    companion object {
        public val SOCIAL_IMAGE_BITE: Array<String> = arrayOf(
            "https://media1.tenor.com/images/d97e4bc853ed48bf83386664956d75ec/tenor.gif?itemid=10364764",
            "https://cdn.weeb.sh/images/S1o6egmjZ.gif",
            "https://cdn.weeb.sh/images/rkakblmiZ.gif",
            "https://cdn.weeb.sh/images/ByWuR1q1M.gif",
            "https://i.kym-cdn.com/photos/images/original/000/832/011/aaa.gif",
            "https://media1.tenor.com/images/17f0fc8bc1e0d5df5f519b8cd9237ac8/tenor.gif?itemid=5384805",
            "https://steamusercontent-a.akamaihd.net/ugc/905653802594683235/4AC33E7E959F58F248185C8D9872B59AD690678B/",
            "https://66.media.tumblr.com/206dbf12d5a0e790796057d34f2f633c/tumblr_o0lb77IGsn1u9u1mvo1_500.gif",
        )
        public val SOCIAL_IMAGE_HUG: Array<String> = arrayOf(
            "https://media.giphy.com/media/qscdhWs5o3yb6/giphy.gif","https://media.giphy.com/media/rSNAVVANV5XhK/giphy.gif",
            "https://media.giphy.com/media/ttThLoTVJb1EQ/giphy.gif",
            "https://thumbs.gfycat.com/JubilantImaginativeCuttlefish-max-1mb.gif",
            "https://media.giphy.com/media/svXXBgduBsJ1u/giphy.gif",
            "https://media.giphy.com/media/C4gbG94zAjyYE/giphy.gif",
            "https://thumbs.gfycat.com/AffectionateWelldocumentedKitfox-small.gif",
            "https://i.pinimg.com/originals/02/7e/0a/027e0ab608f8b84a25b2d2b1d223edec.gif",
            "https://78.media.tumblr.com/f95126745e7f608d3718adae179fad6e/tumblr_o6yw691YXE1vptudso1_500.gif",
            "https://i.pinimg.com/originals/4b/8f/5c/4b8f5ca7bf41461a19e3b4d1e64c1eb5.gif",
            "https://media1.tenor.com/images/6ac90d7bd8c1c3c61e6a317e4abf260e/tenor.gif?itemid=12668472",
            "https://tenor.com/view/warm-hug-gif-10592083",
            "https://c.tenor.com/9e1aE_xBLCsAAAAM/anime-hug.gif",
        )
        public val SOCIAL_IMAGE_KISS: Array<String> = arrayOf(
            "https://cdn.weeb.sh/images/S1-KXsh0b.gif",
            "https://cdn.weeb.sh/images/Skc42pdv-.gif",
            "https://cdn.weeb.sh/images/HJ8dQRYK-.gif",
            "https://cdn.weeb.sh/images/r10UnpOPZ.gif",
            "http://i.skyrock.net/5079/88775079/pics/3174561165_1_11_1IKppSSS.gif",
            "https://media1.tenor.com/images/78095c007974aceb72b91aeb7ee54a71/tenor.gif?itemid=5095865",
            "https://media.giphy.com/media/FqBTvSNjNzeZG/giphy.gif",
            "https://media.giphy.com/media/ZRSGWtBJG4Tza/giphy.gif",
            "https://66.media.tumblr.com/5d51b3bbd64ccf1627dc87157a38e59f/tumblr_n5rfnvvj7H1t62gxao1_500.gif",
            "https://media.tenor.com/images/197df534507bd229ba790e8e1b5f63dc/tenor.gif",
            "https://thumbs.gfycat.com/GoodnaturedRightBarracuda-small.gif",
            "https://i1.wp.com/loveisaname.com/wp-content/uploads/2016/09/23.gif",
            "https://66.media.tumblr.com/946c2015eca37fc7a980d2fa4f993bbe/tumblr_n0h9xv4RXh1t15wswo1_400.gif",
            "https://media1.tenor.com/images/4e43e894e28c27715e995da4c1a02115/tenor.gif?itemid=5982185",
            "https://i.gifer.com/6IoA.gif",
        )
        public val SOCIAL_IMAGE_LICK: Array<String> = arrayOf(
            "https://cdn.weeb.sh/images/Sk15iVlOf.gif",
            "https://cdn.weeb.sh/images/rktygCOD-.gif",
            "https://i.pinimg.com/originals/6c/d0/68/6cd068418b74ab0808009e692a370d9e.gif",
            "https://gifimage.net/wp-content/uploads/2018/10/anime-lick-lips-gif-5.gif",
            "https://cdn.weeb.sh/images/HkEqiExdf.gif",
            "https://cdn.weeb.sh/images/S1Ill0_vW.gif",
            "https://cdn.weeb.sh/images/rykRHmB6W.gif",
            "https://cdn.weeb.sh/images/rkBbBQS6W.gif",
            "https://cdn.weeb.sh/images/Syg8gx0OP-.gif",
        )
        public val SOCIAL_IMAGE_NUZZLE: Array<String> = arrayOf(
            "https://i.imgur.com/UP9CmgY.gif",
            "https://i.imgur.com/ngGm9v9.gif",
            "https://i.imgur.com/EVBa1rR.gif",
            "https://i.imgur.com/DScGTM6.gif",
            "https://i.imgur.com/G2DLX6i.gif",
            "https://i.imgur.com/KrYnQzR.gif",
            "https://media.giphy.com/media/f82EqBTeCEgcU/giphy.gif",
            "https://media.giphy.com/media/BXrwTdoho6hkQ/giphy.gif",
            "https://pa1.narvii.com/6103/377538d76d83ec7d9d2be32870d43f2ac931a412_hq.gif",
            "http://i.imgur.com/p7beIyD.gif",
        )
        public val SOCIAL_IMAGE_SLAP: Array<String> = arrayOf(
            "https://media1.tenor.com/images/1cf84bf514d2abd2810588caf7d9fd08/tenor.gif?itemid=7679403",
            "https://media1.tenor.com/images/fb17a25b86d80e55ceb5153f08e79385/tenor.gif?itemid=7919028",
            "https://thumbs.gfycat.com/PersonalUnlinedAsiaticwildass-size_restricted.gif",
            "https://media1.tenor.com/images/b6d8a83eb652a30b95e87cf96a21e007/tenor.gif?itemid=10426943",
            "https://i.imgur.com/Agwwaj6.gif",
            "https://media1.tenor.com/images/4eed54377433c396ce2d9ad9ee5d22ef/tenor.gif?itemid=11234788",
            "https://gifimage.net/wp-content/uploads/2017/07/anime-slap-gif-12.gif",
            "https://i.pinimg.com/originals/4e/9e/a1/4e9ea150354ad3159339b202cbc6cad9.gif",
            "https://i.imgur.com/mIg8erJ.gif",
            "https://thumbs.gfycat.com/PleasedShorttermHorsemouse-size_restricted.gif",
        )
        public val SOCIAL_IMAGE_HEADPAT: Array<String> = arrayOf(
            "https://i.imgur.com/ptTqEf5.gif",
            "https://i.imgur.com/j5ks2VL.gif",
            "https://i.imgur.com/uH9bDRf.gif",
            "https://i.imgur.com/mo6N02V.gif",
            "https://i.imgur.com/dfwEkiH.gif",
            "https://i.imgur.com/FrETUkP.gif",
            "https://i.imgur.com/4chvQoC.gif",
            "https://i.imgur.com/Dfrn1qs.gif",
            "https://i.imgur.com/YDsLin7.gif",
            "https://i.imgur.com/U6WV6nf.gif",
        )
    }
}