# spec-grind

Treating clojure spec as a meat grinder.
Very basic and in alpha.

```clj
[spec-grind "0.1.1"]
```

### Usage

require:
```clj
[spec-grind.grind :as g]
```

Validate and coerce values at the same time (conform):
```clj
;; string to keyword
> (s/conform g/keyword "a-key")
:a-key

;; string to UUID
> (s/conform g/uuid "58cd0579-fa0b-450e-ac6b-92170ba0f296")
#uuid"58cd0579-fa0b-450e-ac6b-92170ba0f296"

;; string to instant (date)
> (s/conform g/inst "2017-03-18T07:42:34.173-00:00")
#inst"2017-03-18T07:42:34.173-00:00"

;; long to instant
> (s/conform g/inst 1489822954173)
#inst"2017-03-18T07:42:34.173-00:00"

;; string to boolean
> (s/conform g/boolean "true")
true
```

Target types stay the same:
```clj
> (s/conform g/boolean true)
true
```

Don't allow unexpected map keys:
```clj
> (s/def ::strict-map (g/keyz :req [::a]
                              :deny :rest))
> (s/conform ::strict-map {::a 1})
{::a 1}
> (s/conform ::strict-map {::a 1 ::b 2}
::s/invalid
```

#### Registry

You can add the conformers to registry and reference them as keywords:

```clj
(:require ; ...
          [clojure.spec :as s]
          [spec-grind.grind :as g]
          [spec-grind.registry]
          ; ...
          )

(s/def ::user-id ::g/uuid)
(s/def ::user (g/keyz :req [::user-id]
                      :deny :rest))

(s/conform ::user {::user-id ...})
```

## License

Copyright © 2017 Yonatan Elhanan

Distributed under the MIT License.
