(ns meinsweeper.core-spec
  (:require [speclj.core :refer :all]
            [clojure.core.logic :as lg]
            [meinsweeper.core :refer :all]))

(defn remove-facts [relation]
  (let [facts (lg/run* [a b]
                       (relation a b)
                       )]
    (lg/retractions relation facts)
    ))

(defmacro remove-facts [relation n-args]
  (let [argvector (vec (repeatedly n-args gensym))]
    `(let [facts# (lg/run* ~argvector
                       (~relation ~@argvector))]
       (lg/retractions ~relation (vec facts#)))))

(describe
  "integration"
  (before
    (remove-facts neighbor-mine-count 2))

  (it
    "finds values for coordinates fixed by constraints"
    (let [constraints-set #{
                            (new-constraint #{[0 0] [0 1]} 1)
                            (new-constraint #{[0 0] [0 1] [0 2]} 2)}]
      (should= {[0 2] 1} (fixed-coordinate-values constraints-set))))

  (it "gets constraints from board dimensions + existing facts"
      (lg/fact neighbor-mine-count [0 0] 2)
      (lg/fact neighbor-mine-count [0 1] 3)

      (should= #{
                 (new-constraint #{[0 0]} 0)
                 (new-constraint #{[0 1]} 0)
                 (new-constraint #{[0 0] [0 1] [1 0] [1 1]}  2)
                 (new-constraint #{[0 0] [0 1] [0 2] [1 0] [1 1] [1 2]} 3)}
               (constraints 2 3)))
  )

