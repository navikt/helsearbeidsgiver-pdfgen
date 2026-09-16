#import "/templates/fritakagp/shared.typ": *
#import "/templates/fritakagp/krav.typ": render-krav
#let data = json("/data/fritakagp/kronisk-krav.json")
#set document(title: "Krav om refusjon for arbeidsgiverperiode - Kronisk syk", author: "Nav", keywords: ("Nav", "krav"))
#render-krav(data, "Krav om refusjon for arbeidsgiverperiode - Kronisk syk")
