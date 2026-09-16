#import "/templates/fritakagp/shared.typ": *
#import "/templates/fritakagp/krav.typ": render-krav
#let data = json("/data/fritakagp/gravid-krav.json")
#set document(title: "Krav om refusjon for arbeidsgiverperiode - Gravid", author: "Nav", keywords: ("Nav", "krav"))
#render-krav(data, "Krav om refusjon for arbeidsgiverperiode - Gravid")
