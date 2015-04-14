(ns boot.boot-macros)

(defmacro get-env [k]
  `(quote ~(System/getProperty k)))
